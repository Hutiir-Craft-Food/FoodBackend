package com.gmail.ypon2003.marketplacebackend.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;



@OpenAPIDefinition(
        info = @Info(
                contact =  @Contact(),
                title = "HutirCraft",
                description = "Documentation for access to the HutirCraft resources via REST API.",
                version = "1.2.1"
        )
)
@SecurityScheme(
        name = BEARER_AUTHENTICATION,
        description = "JWT auth description",
        scheme = BEARER,
        type = SecuritySchemeType.HTTP,
        bearerFormat = JWT,
        in = SecuritySchemeIn.HEADER
)
public class OpenApiSwaggerConfig { }