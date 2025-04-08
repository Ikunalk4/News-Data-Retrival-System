package com.inshort.newshub.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {
	
	@Value("${openapi.server.url}")
	private String openApiServerUrl;

	 @Bean
	    public OpenAPI newsHubServiceAPI(){
	        return new OpenAPI()
	                .info(
	                        new Info()
	                                .title("News Hub Service API")
	                                .description("This is the REST API for News Hub Service")
	                                .version("0.1")
	                                .license(new License()
	                                        .name("Inshort")))
	                .externalDocs(new ExternalDocumentation()
	                        .description("You can refer to the Source of Truth Service Wiki Documentation")
	                        .url("https://new-hub-service-dummy-url.com/docs"))
	                .servers(Collections.singletonList(new Server().url(openApiServerUrl).description("Default server url")));

	    }
}