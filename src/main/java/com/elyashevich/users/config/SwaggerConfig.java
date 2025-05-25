package com.elyashevich.users.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String USER_SERVICE_TITLE = "User service application";
    private static final String USER_SERVICE_DESCRIPTION = "This is a sample API documentation using Swagger";
    private static final String USER_SERVICE_VERSION = "1.0";

    @Value("${application.open-api.email}")
    private String email;

    @Value("${application.open-api.server}")
    private String serverUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(
                        List.of(
                                new Server().url(this.serverUrl)
                        )
                )
                .info(
                        new Info()
                                .title(USER_SERVICE_TITLE)
                                .description(USER_SERVICE_DESCRIPTION)
                                .version(USER_SERVICE_VERSION)
                                .contact(new Contact().email(this.email))
                );
    }
}