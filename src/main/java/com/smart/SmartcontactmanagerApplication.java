package com.smart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.smart.entities")
@EnableJpaRepositories(basePackages = "com.smart.dao")  // тут лежат UserRepository и др. репозитории
public class SmartcontactmanagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartcontactmanagerApplication.class, args);
    }

}
