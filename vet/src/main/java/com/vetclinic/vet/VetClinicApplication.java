package com.vetclinic.vet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.vetclinic.vet"})
@EntityScan(basePackages = {"com.vetclinic.vet.model"})
@EnableJpaRepositories(basePackages = {"com.vetclinic.vet.repository"})
public class VetClinicApplication {

    public static void main(String[] args) {
        SpringApplication.run(VetClinicApplication.class, args);
    }
    
    // The application is configured to:
    // 1. Scan for components in com.vetclinic.vet and sub-packages
    // 2. Look for JPA entities in com.vetclinic.vet.model
    // 3. Find JPA repositories in com.vetclinic.vet.repository
    // 4. Use auto-configuration for Spring Boot features
}
