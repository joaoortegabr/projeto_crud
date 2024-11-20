package com.project.models.mocks;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.project.entities.Customer;
import com.project.models.enums.Datastate;

@Component
public class CustomerMock {

	public Customer single() {
		
		Customer customer = new Customer(
            "João Silva",
            "27802535093",
            "joao@email.com",
            "11999998888",
            "São Paulo",
            "SP",
            "Brasil",
            LocalDate.now(),
            true,
            Datastate.ATIVO);
		customer.setId(1L);
		
		return customer;
	}
	
	public List<Customer> list() {
		
		Customer customer1 = new Customer(
            "João Silva",
            "27802535093",
            "joao@email.com",
            "11999998888",
            "São Paulo",
            "SP",
            "Brasil",
            LocalDate.now(),
            true,
            Datastate.ATIVO);
		
		Customer customer2 = new Customer(
            "Maria Pinheiro",
            "54564091000",
            "maria@email.com",
            "21888887777",
            "Rio de Janeiro",
            "RJ",
            "Brasil",
            LocalDate.now(),
            true,
            Datastate.ATIVO);

		customer1.setId(1L);
		customer2.setId(2L);
		
		List<Customer> list = new ArrayList<>();
		list.add(customer1);
		list.add(customer2);
		
		return list;
	}
	
}
