package com.drone.rental.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger API文档配置
 */
@Configuration
@EnableOpenApi
public class SwaggerConfig {

    @Value("${swagger.enabled:true}")
    private boolean enabled;

    @Value("${swagger.title:API文档}")
    private String title;

    @Value("${swagger.description:API接口文档}")
    private String description;

    @Value("${swagger.version:1.0.0}")
    private String version;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .enable(enabled)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.drone.rental.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .version(version)
                .contact(new Contact("毕业设计", "", ""))
                .build();
    }

    /**
     * 配置安全方案(JWT Token)
     */
    private List<SecurityScheme> securitySchemes() {
        List<SecurityScheme> schemes = new ArrayList<>();
        schemes.add(new ApiKey("Authorization", "Authorization", "header"));
        return schemes;
    }

    /**
     * 配置安全上下文
     */
    private List<SecurityContext> securityContexts() {
        List<SecurityContext> contexts = new ArrayList<>();
        contexts.add(SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("^(?!.*(/auth/login|/auth/register)).*$"))
                .build());
        return contexts;
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> references = new ArrayList<>();
        references.add(new SecurityReference("Authorization", authorizationScopes));
        return references;
    }
}
