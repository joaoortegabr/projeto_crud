package com.project.config;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.project.models.mappers.CustomerMapper;

@Configuration
public class MapStructConfig {

	@Bean
    CustomerMapper customerMapper() {
        return Mappers.getMapper(CustomerMapper.class);
    }
	
}
