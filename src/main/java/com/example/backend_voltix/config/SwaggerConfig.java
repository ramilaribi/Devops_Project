package com.example.backend_voltix.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Voltix Backend API")
                        .version("1.0")
                        .description("This is the backend API for the Voltix project. The project is developed by a team of skilled developers and is focused on energy consumption management.")
                        .termsOfService("http://your-terms-of-service-url.com")
                        .contact(new Contact()
                                .name("Voltix Backend Team")
                                .email("support@voltix.com")
                                .url("http://vps-b7fc2ba4.vps.ovh.net:8080/SFMConnect2harmonic/"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("voltix-api")
                .pathsToMatch("/**")
                .build();
    }
}
