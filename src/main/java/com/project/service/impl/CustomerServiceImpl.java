package com.project.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.project.entities.Customer;
import com.project.exceptions.ResourceNotFoundException;
import com.project.models.enums.Datastate;
import com.project.repositories.CustomerRepository;
import com.project.service.CustomerService;
import com.project.utils.PaginationRequest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

@Service
public class CustomerServiceImpl implements CustomerService {
	
	private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
	
	private final CustomerRepository customerRepository;
	
	public CustomerServiceImpl(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}
	
	public Page<Customer> findAll(PaginationRequest paginationRequest) {
		log.info("Executing service findAll");
	    PageRequest pageRequest = PageRequest.of(
	            paginationRequest.getPage(),
	            paginationRequest.getSize(),
	            Sort.by(Sort.Direction.fromString(paginationRequest.getSortDirection()), paginationRequest.getSortField())
	        );
		Page<Customer> customerPage = customerRepository.findAll(pageRequest);

        List<Customer> activeCustomers = customerPage.getContent().stream()
            .filter(customer -> customer.getDatastate() == Datastate.ATIVO)
            .collect(Collectors.toList());

        return new PageImpl<>(activeCustomers, customerPage.getPageable(), customerPage.getTotalElements());
	}

	public Customer findById(Long id) {
		log.info("Executing service findById with param: {}", id);
		Customer customer = customerRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(id));
		if(customer.getDatastate() != null && customer.getDatastate() == Datastate.ATIVO) {
			return customer;
		} else {
			throw new ResourceNotFoundException(id);
		}
	}
	
	public Customer save(Customer customer) {
		log.info("Executing service save with param: {}", customer);
		try {
			if(customer.getRegistrationDate() == null) {
				customer.setRegistrationDate(LocalDate.now());
				customer.setActive(true);
				customer.setDatastate(Datastate.ATIVO);
			}
		} catch(ConstraintViolationException e) {
			throw new ConstraintViolationException("Erro ao validar dados de entrada.", null);
		} catch(DataIntegrityViolationException e) {
			throw new DataIntegrityViolationException("Registro já existe no banco de dados.");
		}
		return customerRepository.save(customer);
	}
	
	public Customer update(Long id, Customer customer) {
		log.info("Executing service update with params: {} and {}", id, customer);
		try {
			Customer entity = findById(id);
			entity = customer;
			return customerRepository.save(entity);
		} catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException(id);
		}
	}
	
	public String delete(Long id) {
		log.info("Executing service delete with param: {}", id);
		Customer customer = findById(id);
		customer.setDatastate(Datastate.INATIVO);
		customerRepository.save(customer);
		return "Registro removido com sucesso.";
	}

	public String toggleActivateAccount(Long id) {
		log.info("Executing service toggleActivation with param: {}", id);
		Customer customer = findById(id);
		if(customer.getActive()) {
			customer.setActive(false);
			customerRepository.save(customer);
			return "Cadastro inativado com sucesso.";
		} else {
			customer.setActive(true);
			customerRepository.save(customer);
			return "Cadastro ativado com sucesso.";
		}
	}

}
