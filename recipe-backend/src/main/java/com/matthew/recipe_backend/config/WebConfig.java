package com.matthew.recipe_backend.config;

import java.io.File;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // TODO: This is local dev only, replaced by S3 in production
        // Relative path did not resolve correctly, using absolute path as workaround

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:C:/Users/billy/OneDrive/Documents/recipes/recipe-backend/uploads/");
    }
}
