package com.project.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.entities.Customer;
import com.project.exceptions.ResourceNotFoundException;
import com.project.models.enums.Datastate;
import com.project.models.mocks.CustomerMock;
import com.project.repositories.CustomerRepository;
import com.project.service.impl.CustomerServiceImpl;
import com.project.utils.PaginationRequest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;
    
    @InjectMocks
    private CustomerServiceImpl customerService;
    
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
    void shouldReturnListOfActiveCustomers() {
        PaginationRequest paginationRequest = new PaginationRequest(0,customerList.size(), "name", "asc");
    	Page<Customer> customerPage = new PageImpl<>(customerList, PageRequest.of(0, customerList.size()), customerList.size());

        when(customerRepository.findAll(any(PageRequest.class))).thenReturn(customerPage);
        Page<Customer> result = customerService.findAll(paginationRequest);

        assertEquals(customerList.size(), result.getContent().size());
        assertEquals(customerList.get(0), result.getContent().get(0));
        assertEquals(customerList.get(1), result.getContent().get(1));
        verify(customerRepository, times(1)).findAll(any(PageRequest.class));
    }
    
    @Test
    @DisplayName("Check if all returned Customers are active")
    void shouldReturnListWithOnlyActiveCustomers() {
    	Customer inactiveCustomer = new Customer();
        inactiveCustomer.setDatastate(Datastate.INATIVO);
        customerList.add(inactiveCustomer);
        PaginationRequest paginationRequest = new PaginationRequest(0, customerList.size(), "name", "asc");
    	Page<Customer> customerPage = new PageImpl<>(customerList, PageRequest.of(0, customerList.size()), customerList.size());
        
        when(customerRepository.findAll(any(PageRequest.class))).thenReturn(customerPage);
        Page<Customer> result = customerService.findAll(paginationRequest);

        assertTrue(result.getContent().stream().allMatch(customer -> customer.getDatastate() == Datastate.ATIVO));
        assertEquals(customerList.size(), result.getSize());
        assertFalse(result.getContent().contains(inactiveCustomer));
        verify(customerRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    @DisplayName("Check if a single Customer is returned successfully")
    void shouldReturnCustomerSuccessfully() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Customer result = customerService.findById(1L);

        assertEquals(customer, result);
        verify(customerRepository, times(1)).findById(1L);
    }
    
    @Test
    @DisplayName("Check if throw Exception when Customer is found but datastate is inactive")
    void shouldThrowExceptionWhenCustomerIsInactive() {
    	customer.setDatastate(Datastate.INATIVO);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        assertThrows(ResourceNotFoundException.class, () -> customerService.findById(1L));
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Check if Exception os thrown when Customer is found but datastate is null")
    void shouldThrowExceptionWhenCustomerNotFound() {
    	Customer deletedCustomer = new Customer();
        deletedCustomer.setDatastate(null);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(deletedCustomer));

        assertThrows(ResourceNotFoundException.class, () -> customerService.findById(1L));
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Check if a new Customer is created")
    void shouldCreateNewCustomer() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        
        customer.setRegistrationDate(null);
        Customer result = customerService.save(customer);

        assertNotNull(result.getRegistrationDate());
        assertTrue(result.getActive());
        assertEquals(Datastate.ATIVO, result.getDatastate());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }
    
    @Test
    @DisplayName("Check if an Customer already exists and can't be created")
    void shouldNotCreateNewCustomerBecauseItAlreadyExists() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        
        customer.setRegistrationDate(LocalDate.now());
        Customer result = customerService.save(customer);

        assertTrue(result.getRegistrationDate().isEqual(LocalDate.now()));
        assertTrue(result.getActive());
        assertEquals(Datastate.ATIVO, result.getDatastate());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }
    
    @Test
    @DisplayName("Check if ConstraintViolationException is correctly thrown")
    void shouldTestThrowsConstraintViolationException() {
        doThrow(new ConstraintViolationException("Erro ao validar dados de entrada.", null)).when(customerRepository).save(customer);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
        	customer.setCpf("05352751");
            customerService.save(customer);
        });

        assertEquals("Erro ao validar dados de entrada.", exception.getMessage());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Check if DataIntegrityViolationExceptio is correctly thrown")
    void shouldTestThrowsDataIntegrityViolationException() {
    	when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        customerService.save(customer);
        
        doThrow(new DataIntegrityViolationException("Registro já existe no banco de dados.")).when(customerRepository).save(customer);

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            customerService.save(customer);
        });

        assertEquals("Registro já existe no banco de dados.", exception.getMessage());
        verify(customerRepository, times(2)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Check if the selected Customer is updated")
    void shouldUpdateCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
        	Customer savedCustomer = invocation.getArgument(0);
            return savedCustomer;
        });

        Customer updatedCustomer = new Customer();
        updatedCustomer.setName("Novo Nome");
        Customer result = customerService.update(1L, updatedCustomer);

        assertEquals("Novo Nome", result.getName());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }
    
    @Test
    @DisplayName("Check if trying to update a non-existing Customer throws Exception")
    void shouldNotUpdateNonExistingCustomer() {
        when(customerRepository.findById(1L)).thenThrow(new EntityNotFoundException());

        assertThrows(ResourceNotFoundException.class, () -> customerService.update(1L, customer));

        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, never()).save(any(Customer.class));
    }
    
    @Test
    @DisplayName("Check if the selected Customer is logic deleted")
    void shouldDeleteCustomerSuccessfully() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        String message = customerService.delete(1L);

        assertEquals("Registro removido com sucesso.", message);
        assertEquals(Datastate.INATIVO, customer.getDatastate());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Check if trying to logic delete a non-existing Customer throws Exception")
    void shouldNotDeleteNonExistingCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.delete(1L));
        verify(customerRepository, never()).save(any(Customer.class));
        verify(customerRepository, times(1)).findById(1L);
    }
    
    @Test
    @DisplayName("Check if the selected Customer is set not active")
    void shouldDeactivateCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        String message = customerService.toggleActivateAccount(1L);

        assertEquals("Cadastro inativado com sucesso.", message);
        assertFalse(customer.getActive());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Check if the selected Customer is set active")
    void shouldActivateCustomer() {
    	customer.setActive(false);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        String message = customerService.toggleActivateAccount(1L);

        assertEquals("Cadastro ativado com sucesso.", message);
        assertTrue(customer.getActive());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }
}

