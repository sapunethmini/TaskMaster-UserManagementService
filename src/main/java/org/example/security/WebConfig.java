package org.example.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
// By implementing WebMvcConfigurer, this class provides the CORS configuration directly.
// This is a cleaner approach than creating a separate @Bean method.
public class WebConfig implements WebMvcConfigurer {

    // Inject the allowed origins property from your application configuration.
    // A default value of "http://localhost:4200" is provided using the colon (:) syntax.
    // This default is used when running the app manually (e.g., from your IDE),
    // which fixes the startup error you were seeing.
    // This will be OVERRIDDEN by the value in your .env or application.properties file if it exists.
    @Value("${cors.allowed.origins:http://localhost:4200}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply CORS to all endpoints in the application
                .allowedOrigins(allowedOrigins) // Use the configured origins from the @Value field
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Specify allowed HTTP methods
                .allowedHeaders("*") // Allow all request headers
                .allowCredentials(true); // Allow credentials (e.g., cookies, authorization headers)
    }
}