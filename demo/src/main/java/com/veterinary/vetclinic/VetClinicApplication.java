package com.veterinary.vetclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.veterinary.vetclinic")
@EntityScan("com.veterinary.vetclinic.model")
@EnableJpaRepositories("com.veterinary.vetclinic.repository")
public class VetClinicApplication {

    public static void main(String[] args) {
        SpringApplication.run(VetClinicApplication.class, args);
    }
}