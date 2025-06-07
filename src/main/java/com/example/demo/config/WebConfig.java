package com.example.demo.config; // 确保包名正确

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 应用于所有端点
                .allowedOrigins("http://localhost:5173") // 允许来自前端开发服务器的请求
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 必须包含 "OPTIONS"
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true);
    }
}