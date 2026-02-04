package com.homesolutions;

import com.homesolutions.config.OpenAPIConfig;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenAPIConfigTest {

    private OpenAPIConfig openAPIConfig;

    @BeforeEach
    void setUp() {
        openAPIConfig = new OpenAPIConfig();
    }

    @Test
    void customOpenAPI_ReturnsConfiguredOpenAPI() {
        // When
        OpenAPI openAPI = openAPIConfig.customOpenAPI();

        // Then
        assertThat(openAPI).isNotNull();
    }

    @Test
    void customOpenAPI_HasCorrectInfo() {
        // When
        OpenAPI openAPI = openAPIConfig.customOpenAPI();
        Info info = openAPI.getInfo();

        // Then
        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isEqualTo("Home Solutions API");
        assertThat(info.getVersion()).isEqualTo("1.0.0");
        assertThat(info.getDescription()).isEqualTo("Secure REST API for On-Demand Home Services Platform");
    }

    @Test
    void customOpenAPI_HasCorrectContactInfo() {
        // When
        OpenAPI openAPI = openAPIConfig.customOpenAPI();
        Contact contact = openAPI.getInfo().getContact();

        // Then
        assertThat(contact).isNotNull();
        assertThat(contact.getName()).isEqualTo("Home Solutions Team");
        assertThat(contact.getEmail()).isEqualTo("support@homesolutions.com");
    }

    @Test
    void customOpenAPI_HasSecurityRequirement() {
        // When
        OpenAPI openAPI = openAPIConfig.customOpenAPI();

        // Then
        assertThat(openAPI.getSecurity()).isNotNull();
        assertThat(openAPI.getSecurity()).hasSize(1);
        SecurityRequirement securityRequirement = openAPI.getSecurity().get(0);
        assertThat(securityRequirement.containsKey("Bearer Authentication")).isTrue();
    }

    @Test
    void customOpenAPI_HasSecurityScheme() {
        // When
        OpenAPI openAPI = openAPIConfig.customOpenAPI();
        Components components = openAPI.getComponents();

        // Then
        assertThat(components).isNotNull();
        assertThat(components.getSecuritySchemes()).isNotNull();
        assertThat(components.getSecuritySchemes()).containsKey("Bearer Authentication");
    }

    @Test
    void customOpenAPI_SecuritySchemeIsHTTPBearer() {
        // When
        OpenAPI openAPI = openAPIConfig.customOpenAPI();
        SecurityScheme securityScheme = openAPI.getComponents().getSecuritySchemes().get("Bearer Authentication");

        // Then
        assertThat(securityScheme).isNotNull();
        assertThat(securityScheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(securityScheme.getScheme()).isEqualTo("bearer");
        assertThat(securityScheme.getBearerFormat()).isEqualTo("JWT");
        assertThat(securityScheme.getDescription()).isEqualTo("Enter JWT token");
    }

    @Test
    void customOpenAPI_ComponentsContainsOnlyOneSecurityScheme() {
        // When
        OpenAPI openAPI = openAPIConfig.customOpenAPI();
        Components components = openAPI.getComponents();

        // Then
        assertThat(components.getSecuritySchemes()).hasSize(1);
    }
}
