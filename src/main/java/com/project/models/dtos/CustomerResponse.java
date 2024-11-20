package com.project.models.dtos;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.models.enums.Datastate;

public record CustomerResponse (
		Long id,
		String name,
		String cpf,
		String email,
		String phone,
		String city,
		String state,
		String country,
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		LocalDate registrationDate,
		Boolean active,
		Datastate datastate
		) implements Serializable {

}
