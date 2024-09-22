package com.example.SampleProject.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;
import java.util.List;

//http://localhost:8080/swagger-ui/index.html#/

@Configuration
public class SwaggerConfig {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    private ApiKey apiKeys(){
        return new ApiKey("JWT",AUTHORIZATION_HEADER,"header");
    }

    private List<SecurityReference> securityReferenceList(){
        AuthorizationScope scope = new AuthorizationScope("global","accessEverything");
        return Collections.singletonList(new SecurityReference("JWT", new AuthorizationScope[]{scope}));
    }

    private List<SecurityContext> securityContexts(){
        return Collections.singletonList(SecurityContext.builder().securityReferences(securityReferenceList()).build());
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .securityContexts(securityContexts())
                .securitySchemes(Collections.singletonList(apiKeys()))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.SampleProject"))
                .paths(PathSelectors.any())
                .build().apiInfo(apiInfoMetaData());
    }

    private ApiInfo apiInfoMetaData() {
        return new ApiInfoBuilder().title("SampleProject")
                .description("Sample Api Documentation")
                .contact(new Contact("StepWay", "https://www.stepwaysoftwares.com", "stepway@gmail.com"))
                .license("License of Apis")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .version("1.0")
                .build();
    }
}

