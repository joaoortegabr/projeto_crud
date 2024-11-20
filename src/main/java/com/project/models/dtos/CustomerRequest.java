package com.project.models.dtos;

import java.io.Serializable;

public record CustomerRequest (
		Long id,
		String name,
		String cpf,
		String email,
		String phone,
		String city,
		String state,
		String country
		) implements Serializable {

}
