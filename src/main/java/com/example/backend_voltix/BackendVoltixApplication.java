package com.example.backend_voltix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
@EnableAspectJAutoProxy
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.backend_voltix.repository")
@EntityScan(basePackages = "com.example.backend_voltix.model")
public class BackendVoltixApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendVoltixApplication.class, args);
    }

}
