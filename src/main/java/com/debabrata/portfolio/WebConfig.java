package com.debabrata.portfolio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Serves uploaded images at /uploads/<file>. */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${app.upload-dir}") private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            Path folder = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(folder);
            registry.addResourceHandler("/uploads/**").addResourceLocations(folder.toUri().toString());
        } catch (Exception e) {
            System.out.println("Could not prepare upload folder: " + e.getMessage());
        }
    }
}
