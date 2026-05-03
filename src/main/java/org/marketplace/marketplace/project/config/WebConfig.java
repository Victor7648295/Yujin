package org.marketplace.marketplace.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Сначала смотрим в исходную папку (туда пишет загрузка фото в dev-режиме),
        // потом в classpath (там лежат сидовые картинки).
        registry.addResourceHandler("/img/**")
                .addResourceLocations(
                        "file:src/main/resources/static/img/",
                        "classpath:/static/img/"
                );
    }
}
