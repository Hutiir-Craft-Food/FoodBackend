package com.khutircraftubackend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.stream.Collectors;

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

        private static final String DATE_TIME_FORMAT = "date-time";
        private static final String STRING_TYPE = "string";
        private static final String OBJECT_TYPE = "object";
        private static final String INTEGER_TYPE = "integer";

        @Bean
        public OpenApiCustomizer globalErrorResponseCustomizer() {
                return openApi -> {
                        configureErrorSchemas(openApi.getComponents());
                };
        }

        private void configureErrorSchemas(Components components) {
                components.addSchemas("ErrorResponse400", createErrorSchema(true));

                components.addSchemas("ErrorResponseGeneric", createErrorSchema(false));
        }

        private Schema<?> createErrorSchema(boolean includeData) {
                Schema<?> schema = new ObjectSchema()
                        .type(OBJECT_TYPE)
                        .addProperty("timestamp", new Schema<String>().type(STRING_TYPE).format(DATE_TIME_FORMAT))
                        .addProperty("status", new Schema<Integer>().type(INTEGER_TYPE))
                        .addProperty("error", new Schema<String>().type(STRING_TYPE))
                        .addProperty("message", new Schema<String>().type(STRING_TYPE))
                        .addProperty("path", new Schema<String>().type(STRING_TYPE));

                if (includeData) {
                        schema.addProperty("data", new Schema<>().type(OBJECT_TYPE).nullable(true));
                }

                return schema;
        }
}
