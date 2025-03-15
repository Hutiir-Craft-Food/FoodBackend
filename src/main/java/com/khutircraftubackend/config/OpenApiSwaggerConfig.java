package com.khutircraftubackend.config;

import com.khutircraftubackend.exception.GlobalErrorResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "KhutirCraft API",
                description = "Documentation for access to the KhutirCraft resources via REST API.",
                version = "1.0.0",
                contact = @Contact(name = "Support", email = "khutircraftu@gmail.com")
        )
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiSwaggerConfig {

        @Bean
        public OpenApiCustomizer globalErrorResponseCustomizer() {
                return openApi -> {
                        // Добавляем схему GlobalErrorResponse в компоненты OpenAPI
                        openApi.getComponents()
                                .addSchemas("GlobalErrorResponse", new Schema<GlobalErrorResponse>()
                                        .type("object")
                                        .addProperty("timestamp", new Schema<Date>().type("string").format("date-time"))
                                        .addProperty("status", new Schema<Integer>().type("integer"))
                                        .addProperty("error", new Schema<String>().type("string"))
                                        .addProperty("message", new Schema<String>().type("string"))
                                        .addProperty("path", new Schema<String>().type("string"))
                                        .addProperty("data", new Schema<>().type("object").nullable(true)));

                        openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
                             operation.getResponses().addApiResponse("400", new ApiResponse()
                                .description("Bad Request")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<GlobalErrorResponse>().$ref("#/components/schemas/GlobalErrorResponse")))));

                        operation.getResponses().addApiResponse("401", new ApiResponse()
                                .description("Unauthorized")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<GlobalErrorResponse>().$ref("#/components/schemas/GlobalErrorResponse")))));


                        operation.getResponses().addApiResponse("500", new ApiResponse()
                        .description("Internal Server Error")
                        .content(new Content().addMediaType("application/json",
                                new MediaType().schema(new Schema<GlobalErrorResponse>().$ref("#/components/schemas/GlobalErrorResponse")))));
                        }));
                };
        }
}
