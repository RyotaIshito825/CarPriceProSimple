package com.techacademy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class CarPriceProSimpleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarPriceProSimpleApplication.class, args);
    }

}
